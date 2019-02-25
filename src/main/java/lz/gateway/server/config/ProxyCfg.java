package lz.gateway.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import lz.gateway.server.forward.impl.DubboForward;

@Configuration
public class ProxyCfg {

    @Bean("dubboForward")
    protected DubboForward dubboForward(ApplicationConfig applicationConfig,
        RegistryConfig registryConfig) {
      return new DubboForward(applicationConfig, registryConfig);
    }
}
