package com.nbsaas.boot.entity.tenant;

import com.nbsaas.boot.hibernate.data.entity.AbstractEntity;
import lombok.Data;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public class TenantEntity extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Tenant tenant;


}
