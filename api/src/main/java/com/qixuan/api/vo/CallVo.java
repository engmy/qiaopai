package com.qixuan.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

@Data
public class CallVo implements Serializable
{
    @Id
    private String callId;

    private String pingVal;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss:SSS")
    private Date currentTime;
}
