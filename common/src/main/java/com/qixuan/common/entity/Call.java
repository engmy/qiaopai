package com.qixuan.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qixuan.common.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "call")
public class Call extends BaseEntity
{
    @Id
    private String callId;

    private String pingVal;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss:SSS")
    private Date currentTime;
}
