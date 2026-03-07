package com.github.b3kt.application.properties;

import com.github.b3kt.domain.model.Permission;
import io.smallrye.config.ConfigMapping;

import java.util.List;
import java.util.Map;

/**
 * Mapping of roles to their granular permissions.
 */
@ConfigMapping(prefix = "app.rbac")
public interface RbacProperties {
    
    /**
     * Get the list of permissions for each role.
     * 
     * @return a map from role name to a list of permissions.
     */
    Map<String, List<Permission>> roles();
}
