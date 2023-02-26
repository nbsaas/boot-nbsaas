package com.nbsaas.boot.user.api.domain.request;

import com.nbsaas.boot.rest.request.PageRequest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 搜索bean
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StructureSearchRequest extends PageRequest implements Serializable {

    /**
     * 序列化参数
     */
    private static final long serialVersionUID = 1L;

}