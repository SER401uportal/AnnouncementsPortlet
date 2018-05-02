package org.jasig.portlet.announcements.repository;

import org.jasig.portlet.announcements.model.Topic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TopicRepository extends CrudRepository<Topic, Long> {

    @Query("select t from Topic t where t.subscriptionMethod = 4")
    Optional<Topic> getEmergencyTopic();

    Optional<Topic> findById(Long id);
}
