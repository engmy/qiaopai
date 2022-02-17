package com.qixuan.admin.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qixuan.common.entity.ApiLog;
import lombok.Data;

@Data
public class ApiLogVo extends ApiLog
{
    @JsonIgnore
    private String params;
}