package com.qixuan.admin.config;

import com.qixuan.common.entity.Admin;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Configuration
@EnableMongoAuditing
public class MongodbAuditorAware implements AuditorAware<String>
{
    @Resource
    private HttpSession session;

    @Override
    public Optional<String> getCurrentAuditor()
    {
        Admin admin = (Admin) session.getAttribute("admin");
        if(admin!=null)
        {
            return Optional.of(admin.getUsername());
        }else{
            return Optional.empty();
        }
    }
}
