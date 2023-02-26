package com.nbsaas.boot.system.api.domain.simple;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import lombok.Data;
/**
* 列表对象
*/
@Data
public class SequenceSimple implements Serializable {

/**
* 序列化参数
*/
private static final long serialVersionUID = 1L;

            //@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
        private Date addDate;
            //@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
        private Date lastDate;
        private Integer increment;
        private Long currentNum;
            //@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
        private Date updateDate;
        private String name;
            //@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
        private Date createDate;

}