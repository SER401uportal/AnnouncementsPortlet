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

import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.model.TopicSubscription;
import org.jasig.portlet.announcements.repository.AnnouncementRepository;
import org.jasig.portlet.announcements.repository.TopicRepository;
import org.jasig.portlet.announcements.repository.TopicSubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 */
@Service
@Transactional
@SuppressWarnings("unused")
public class HibernateAnnouncementService implements IAnnouncementService {

    private static Log log = LogFactory.getLog(HibernateAnnouncementService.class);

    private TopicRepository topicRepository;

    private AnnouncementRepository announcementRepository;

    private TopicSubscriptionRepository topicSubscriptionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public HibernateAnnouncementService(TopicRepository topicRepository,
                                        AnnouncementRepository announcementRepository,
                                        TopicSubscriptionRepository topicSubscriptionRepository,
                                        EntityManager entityManager) {
        this.topicRepository = topicRepository;
        this.announcementRepository = announcementRepository;
        this.topicSubscriptionRepository = topicSubscriptionRepository;
        this.entityManager = entityManager;
    }

    /**
     * Fetch all the Topics from the database and return them as a list
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Topic> getAllTopics() {
        return Lists.newArrayList(topicRepository.findAll());
    }

    @SuppressWarnings("unchecked")
    public Topic getEmergencyTopic() {
        return topicRepository.getEmergencyTopic()
                .orElseThrow(() -> new EntityNotFoundException("Emergency topic not found"));
    }

    public void addOrSaveTopic(Topic topic) {
        log.debug("Insert or save topic: [topicId: "
                + (topic.getId() != null ? topic.getId().toString() : "NEW")
                + "]");
        topicRepository.save(topic);
    }

    public void persistTopic(Topic topic) {
        log.debug("Persisting topic: [topicId: " + topic.getId().toString() + "]");
        entityManager.persist(topic);
    }

    public void mergeTopic(Topic topic) {
        log.debug("Merging topic: [topicId: " + topic.getId().toString() + "]");
        entityManager.merge(topic);
    }

    public void addOrSaveAnnouncement(Announcement ann) {
        if (ann.getCreated() == null) {
            ann.setCreated(new Date());
        }
        log.debug("Insert or save announcement: [annId: "
                + (ann.getId() != null ? ann.getId().toString() : "NEW")
                + "]");
        announcementRepository.save(ann);
    }

    public void mergeAnnouncement(Announcement ann) {
        log.debug("Merge announcement: [annId: "
                + (ann.getId() != null ? ann.getId().toString() : "NEW")
                + "]");
        entityManager.merge(ann);
    }

    /**
     * Lookup the specified topic id and return it from the database
     *
     * @param id
     * @return the requested Topic
     * @throws PortletException if called with a null parameter or if the requested topic is invalid
     */
    @SuppressWarnings("unchecked")
    public Topic getTopic(Long id) throws PortletException {

        if (id == null) {
            throw new PortletException("Programming error: getTopic called with null parameter");
        }

        return topicRepository.findById(id)
                .orElseThrow(() ->
                        new PortletException("The requested topic [" + id.toString() + "] does not exist."));
    }

    @SuppressWarnings("unchecked")
    public Announcement getAnnouncement(Long id) throws PortletException {

        if (id == null) {
            throw new PortletException("Programming error: getAnnouncement called with null parameter");
        }

        return announcementRepository.findById(id)
                .orElseThrow(() ->
                        new PortletException("The requested announcement [" + id.toString() + "] does not exist."));
    }

    @SuppressWarnings("unchecked")
    public void deleteAnnouncementsPastCurrentTime() {
        int count = entityManager
                .createQuery("delete from Announcement where END_DISPLAY < current_timestamp()")
                .executeUpdate();
        log.info("Deleted " + count + " expired announcements that stopped displaying prior to now.");
    }

    @SuppressWarnings("unchecked")
    public void deleteAnnouncementsPastExpirationThreshold(int numDays) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, (numDays * -1));
        int count = entityManager
                .createQuery("delete from Announcement where END_DISPLAY < :date")
                .setParameter("date", cal)
                .executeUpdate();
        log.info("Deleted "
                + count
                + " expired announcements that stopped displaying prior to "
                + cal.getTime());
    }

    /**
     * @param request
     * @return
     * @throws PortletException
     */
    @SuppressWarnings("unchecked")
    public List<TopicSubscription> getTopicSubscriptionFor(PortletRequest request) {
        return topicSubscriptionRepository.getTopicSubscriptionFor(request.getRemoteUser());
    }

    public void addOrSaveTopicSubscription(List<TopicSubscription> subs) {
        topicSubscriptionRepository.save(subs);
    }

    public void persistTopicSubscription(List<TopicSubscription> subs) {
        subs.forEach(ts -> entityManager.persist(ts));
    }

    @SuppressWarnings("unchecked")
    public void deleteTopic(Topic topic) {
        // any topic subscriptions with this id should be trashed first (since the topic is not aware of
        // what topic subscriptions exist for it)
        List<TopicSubscription> result =
                topicSubscriptionRepository.getTopicSubscriptionByTopicId(topic.getId());
        result.forEach(ts -> topicSubscriptionRepository.delete(ts));
        // then delete the topic itself (announcements get deleted by hibernate)
        topicRepository.delete(topic);
    }

    public void deleteAnnouncement(Announcement ann) {
        announcementRepository.delete(ann);
    }

    public void deleteTopicSubscription(TopicSubscription sub) {
        topicSubscriptionRepository.delete(sub);
    }
}
