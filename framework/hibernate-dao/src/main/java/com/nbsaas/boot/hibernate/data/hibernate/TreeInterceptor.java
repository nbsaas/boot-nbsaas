package com.nbsaas.boot.hibernate.data.hibernate;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.EmptyInterceptor;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TreeInterceptor extends EmptyInterceptor implements
        ApplicationContextAware {
    public static final String SESSION_FACTORY = "initSessionFactory";
    private static final Logger log = LoggerFactory
            .getLogger(TreeInterceptor.class);
    private ApplicationContext appCtx;
    private SessionFactory sessionFactory;

    public void setApplicationContext(ApplicationContext appCtx)
            throws BeansException {
        this.appCtx = appCtx;
    }

    protected SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = appCtx.getBean(SESSION_FACTORY,
                    SessionFactory.class);
            if (sessionFactory == null) {
                throw new IllegalStateException("not found bean named '"
                        + SESSION_FACTORY
                        + "',please check you spring config file.");
            }
        }
        return sessionFactory;
    }

    protected Session getSession() {
        return getSessionFactory().getCurrentSession();
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state,
                          String[] propertyNames, Type[] types) {
        if (entity instanceof HibernateTree) {
            HibernateTree<?> tree = (HibernateTree<?>) entity;
            Number parentId = tree.getParentId();
            String beanName = tree.getClass().getName();
            Session session = getSession();
            FlushMode model = session.getHibernateFlushMode();
            session.setHibernateFlushMode(FlushMode.MANUAL);
            Integer myPosition;
            if (parentId != null) {
                // ?????????????????????null?????????????????????????????????
                String hql = "select bean." + tree.getRgtName() + " from "
                        + beanName + " bean where bean.id=:pid";
                myPosition = ((Number) session.createQuery(hql).setParameter(
                        "pid", parentId).uniqueResult()).intValue();
                String hql1 = "update " + beanName + " bean set bean."
                        + tree.getRgtName() + " = bean." + tree.getRgtName()
                        + " + 2 WHERE bean." + tree.getRgtName()
                        + " >= :myPosition";
                String hql2 = "update " + beanName + " bean set bean."
                        + tree.getLftName() + " = bean." + tree.getLftName()
                        + " + 2 WHERE bean." + tree.getLftName()
                        + " >= :myPosition";
                if (!StringUtils.isBlank(tree.getTreeCondition())) {
                    hql1 += " and (" + tree.getTreeCondition() + ")";
                    hql2 += " and (" + tree.getTreeCondition() + ")";
                }
                session.createQuery(hql1)
                        .setParameter("myPosition", myPosition).executeUpdate();
                session.createQuery(hql2)
                        .setParameter("myPosition", myPosition).executeUpdate();
            } else {
                // ?????????????????????????????????
                String hql = "select max(bean." + tree.getRgtName() + ") from "
                        + beanName + " bean";
                if (!StringUtils.isBlank(tree.getTreeCondition())) {
                    hql += " where " + tree.getTreeCondition();
                }
                Number myPositionNumber = (Number) session.createQuery(hql)
                        .uniqueResult();
                // ?????????????????????0
                if (myPositionNumber == null) {
                    myPosition = 1;
                } else {
                    myPosition = myPositionNumber.intValue() + 1;
                }
            }
            session.setHibernateFlushMode(model);
            for (int i = 0; i < propertyNames.length; i++) {
                if (propertyNames[i].equals(tree.getLftName())) {
                    state[i] = myPosition;
                }
                if (propertyNames[i].equals(tree.getRgtName())) {
                    state[i] = myPosition + 1;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id,
                                Object[] currentState, Object[] previousState,
                                String[] propertyNames, Type[] types) {
        if (!(entity instanceof HibernateTree)) {
            return false;
        }
        HibernateTree<?> tree = (HibernateTree<?>) entity;
        for (int i = 0; i < propertyNames.length; i++) {
            if (propertyNames[i].equals(tree.getParentName())) {
                HibernateTree<?> preParent = (HibernateTree<?>) previousState[i];
                HibernateTree<?> currParent = (HibernateTree<?>) currentState[i];
                return updateParent(tree, preParent, currParent);
            }
        }
        return false;
    }

    private boolean updateParent(HibernateTree<?> tree,
                                 HibernateTree<?> preParent, HibernateTree<?> currParent) {
        // ??????????????????????????????????????????????????????
        if ((preParent == null && currParent == null)
                || (preParent != null && currParent != null && preParent
                .getId().equals(currParent.getId()))) {
            return false;
        }
        String beanName = tree.getClass().getName();
        if (log.isDebugEnabled()) {
            log.debug("update Tree {}, id={}, "
                            + "pre-parent id={}, curr-parent id={}", beanName, tree.getId(),
                    preParent == null ? null : preParent.getId(),
                    currParent == null ? null : currParent.getId());
        }
        Session session = getSession();
        // ?????????????????????????????????????????????
        FlushMode model = session.getHibernateFlushMode();
        session.setHibernateFlushMode(FlushMode.MANUAL);
        // ?????????????????????????????????????????????????????????????????????
        Integer currParentRgt;
        if (currParent != null) {
            // ??????????????????
            String hql = "select bean." + tree.getLftName() + ",bean."
                    + tree.getRgtName() + " from " + beanName
                    + " bean where bean.id=:id";
            Object[] position = (Object[]) session.createQuery(hql)
                    .setParameter("id", tree.getId()).uniqueResult();
            int nodeLft = ((Number) position[0]).intValue();
            int nodeRgt = ((Number) position[1]).intValue();
            int span = nodeRgt - nodeLft + 1;
            log.debug("current node span={}", span);

            // ??????????????????????????????
            Object[] currPosition = (Object[]) session.createQuery(hql)
                    .setParameter("id", currParent.getId()).uniqueResult();
            int currParentLft = ((Number) currPosition[0]).intValue();
            currParentRgt = ((Number) currPosition[1]).intValue();
            log.debug("current parent lft={} rgt={}", currParentLft,
                    currParentRgt);

            // ????????????
            String hql1 = "update " + beanName + " bean set bean."
                    + tree.getRgtName() + " = bean." + tree.getRgtName()
                    + " + " + span + " WHERE bean." + tree.getRgtName()
                    + " >= :parentRgt";
            String hql2 = "update " + beanName + " bean set bean."
                    + tree.getLftName() + " = bean." + tree.getLftName()
                    + " + " + span + " WHERE bean." + tree.getLftName()
                    + " >= :parentRgt";
            if (!StringUtils.isBlank(tree.getTreeCondition())) {
                hql1 += " and (" + tree.getTreeCondition() + ")";
                hql2 += " and (" + tree.getTreeCondition() + ")";
            }
            session.createQuery(hql1).setInteger("parentRgt", currParentRgt)
                    .executeUpdate();
            session.createQuery(hql2).setInteger("parentRgt", currParentRgt)
                    .executeUpdate();
            log.debug("vacated span hql: {}, {}, parentRgt={}", hql1, hql2, currParentRgt);
        } else {
            // ?????????????????????????????????
            String hql = "select max(bean." + tree.getRgtName() + ") from "
                    + beanName + " bean";
            if (!StringUtils.isBlank(tree.getTreeCondition())) {
                hql += " where " + tree.getTreeCondition();
            }
            currParentRgt = ((Number) session.createQuery(hql).uniqueResult())
                    .intValue();
            currParentRgt++;
            log.debug("max node left={}", currParentRgt);
        }

        // ???????????????
        String hql = "select bean." + tree.getLftName() + ",bean."
                + tree.getRgtName() + " from " + beanName
                + " bean where bean.id=:id";
        Object[] position = (Object[]) session.createQuery(hql).setParameter(
                "id", tree.getId()).uniqueResult();
        int nodeLft = ((Number) position[0]).intValue();
        int nodeRgt = ((Number) position[1]).intValue();
        int span = nodeRgt - nodeLft + 1;
        if (log.isDebugEnabled()) {
            log.debug("before adjust self left={} right={} span={}",
                    nodeLft, nodeRgt, span);
        }
        int offset = currParentRgt - nodeLft;
        hql = "update " + beanName + " bean set bean." + tree.getLftName()
                + "=bean." + tree.getLftName() + "+:offset, bean."
                + tree.getRgtName() + "=bean." + tree.getRgtName()
                + "+:offset WHERE bean." + tree.getLftName()
                + " between :nodeLft and :nodeRgt";
        if (!StringUtils.isBlank(tree.getTreeCondition())) {
            hql += " and (" + tree.getTreeCondition() + ")";
        }
        session.createQuery(hql).setParameter("offset", offset).setParameter(
                        "nodeLft", nodeLft).setParameter("nodeRgt", nodeRgt)
                .executeUpdate();
        if (log.isDebugEnabled()) {
            log.debug("adjust self hql: {}, offset={}, nodeLft={}, nodeRgt={}",
                    hql, offset, nodeLft, nodeRgt);
        }

        // ??????????????????????????????
        String hql1 = "update " + beanName + " bean set bean."
                + tree.getRgtName() + " = bean." + tree.getRgtName() + " - "
                + span + " WHERE bean." + tree.getRgtName() + " > :nodeRgt";
        String hql2 = "update " + beanName + " bean set bean."
                + tree.getLftName() + " = bean." + tree.getLftName() + " - "
                + span + " WHERE bean." + tree.getLftName() + " > :nodeRgt";
        if (!StringUtils.isBlank(tree.getTreeCondition())) {
            hql1 += " and (" + tree.getTreeCondition() + ")";
            hql2 += " and (" + tree.getTreeCondition() + ")";
        }
        session.createQuery(hql1).setParameter("nodeRgt", nodeRgt)
                .executeUpdate();
        session.createQuery(hql2).setParameter("nodeRgt", nodeRgt)
                .executeUpdate();
        if (log.isDebugEnabled()) {
            log.debug("clear span hql1:{}, hql2:{}, nodeRgt:{}", hql1, hql2, nodeRgt);
        }
        session.setHibernateFlushMode(model);
        return true;
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state,
                         String[] propertyNames, Type[] types) {
        if (entity instanceof HibernateTree) {
            HibernateTree<?> tree = (HibernateTree<?>) entity;
            String beanName = tree.getClass().getName();
            Session session = getSession();
            FlushMode model = session.getHibernateFlushMode();
            session.setHibernateFlushMode(FlushMode.MANUAL);
            String hql = "select bean." + tree.getLftName() + " from "
                    + beanName + " bean where bean.id=:id";
            Integer myPosition = ((Number) session.createQuery(hql)
                    .setParameter("id", tree.getId()).uniqueResult())
                    .intValue();
            String hql1 = "update " + beanName + " bean set bean."
                    + tree.getRgtName() + " = bean." + tree.getRgtName()
                    + " - 2 WHERE bean." + tree.getRgtName() + " > :myPosition";
            String hql2 = "update " + beanName + " bean set bean."
                    + tree.getLftName() + " = bean." + tree.getLftName()
                    + " - 2 WHERE bean." + tree.getLftName() + " > :myPosition";
            if (!StringUtils.isBlank(tree.getTreeCondition())) {
                hql1 += " and (" + tree.getTreeCondition() + ")";
                hql2 += " and (" + tree.getTreeCondition() + ")";
            }
            session.createQuery(hql1).setInteger("myPosition", myPosition)
                    .executeUpdate();
            session.createQuery(hql2).setInteger("myPosition", myPosition)
                    .executeUpdate();
            session.setHibernateFlushMode(model);
        }
    }
}