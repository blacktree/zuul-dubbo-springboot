package lz.gateway.server.security.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
 
@Component
//@ConfigurationProperties(prefix="gateway.security.enabled")
public class SecurityCommonProperties {
	
	@Value("${gateway.security.enabled}")
	private boolean enabled;

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
