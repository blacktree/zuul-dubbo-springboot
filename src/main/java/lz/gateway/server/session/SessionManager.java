package lz.gateway.server.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SessionManager
{

    @Autowired
    private RedisTemplate redisTemplate;
 
    public void removeSession(String sessionId)
    {
        Session session = new Session(redisTemplate, sessionId);
        session.destroy();
    }

    public Session getSession(String sessionId)
    {
        return new Session(redisTemplate, sessionId);
    }
}
