package okr.etl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Holds jira access related data. 
 *
 * @author isidenica
 *
 */
@Service
public class JiraAuthenticationProvider {

    @Value("${jira.username}")
    private String username;

    @Value("${jira.password}")
    private String password;

    @Value("${jira.url}")
    private String url;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
