/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.announcements.service;

import org.jasig.portlet.announcements.model.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import javax.portlet.PortletRequest;

@Component
public class UserPermissionCheckerFactory implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(UserPermissionCheckerFactory.class);
    private static final String CACHE_KEY_DELIM = "|";
    private static final String CACHE_NAME = "userPermissionCheckerCache";

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UserIdService userIdService;

    private Cache cache = null;

    public UserPermissionChecker createUserPermissionChecker(PortletRequest request, Topic topic) {

        String key = getCacheKey(request, topic);
        UserPermissionChecker checker = cache.get(key, UserPermissionChecker.class);
        if (checker == null) {
            if (logger.isTraceEnabled()) {
                logger.trace("Creating cache entry for " + key);
            }
            UserPermissionChecker value = new UserPermissionChecker(request, topic);
            cache.put(key, value);
            return value;

        } else {
            if (logger.isTraceEnabled()) {
                logger.trace("Successfully retrieved cache entry for " + key);
            }
            return checker;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        cache = cacheManager.getCache(CACHE_NAME);
        if (cache == null) {
            throw new BeanCreationException("Required " + CACHE_NAME + " could not be loaded.");
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(CACHE_NAME + " created.");
            }
        }
    }

    private String getCacheKey(PortletRequest request, Topic topic) {
        String userId = userIdService.getUserId(request);
        return new StringBuilder(userId).append(CACHE_KEY_DELIM).append(topic.getTitle()).toString();
    }
}
