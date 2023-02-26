package com.nbsaas.boot.user.api.domain.request;

import com.nbsaas.boot.rest.request.RequestId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 请求对象
 */
@Data
public class UserRoleCatalogDataRequest implements Serializable, RequestId {

    /**
     * 序列化参数
     */
    private static final long serialVersionUID = 1L;


    private Date addDate;
    private Date lastDate;
    private Integer lft;
    private Integer rgt;
    private String ids;
    private Integer levelInfo;
    private Integer sortNum;
    private String name;
    private String code;
    private Integer parent;
    private Integer id;
}