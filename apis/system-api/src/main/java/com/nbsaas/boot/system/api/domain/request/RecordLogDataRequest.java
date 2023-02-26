package com.nbsaas.boot.system.api.domain.request;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import lombok.Data;
import com.nbsaas.boot.rest.request.RequestId;
/**
* 请求对象
*/
@Data
public class RecordLogDataRequest implements Serializable,RequestId {

/**
* 序列化参数
*/
private static final long serialVersionUID = 1L;


        private Date addDate;
        private Date lastDate;
        private String ip;
        private String createName;
        private String data;
        private String title;
        private Long createUser;
        private Date createDate;
        private Long id;
        private String app;
}