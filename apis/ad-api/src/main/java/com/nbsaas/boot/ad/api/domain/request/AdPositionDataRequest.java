package com.nbsaas.boot.ad.api.domain.request;

import com.nbsaas.boot.rest.request.RequestId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 请求对象
 */
@Data
public class AdPositionDataRequest implements Serializable, RequestId {

    /**
     * 序列化参数
     */
    private static final long serialVersionUID = 1L;


    private Date addDate;
    private Date lastDate;
    private String note;
    private String key;
    private Integer height;
    private String template;
    private Integer sortNum;
    private String name;
    private Long id;
    private Integer width;
}