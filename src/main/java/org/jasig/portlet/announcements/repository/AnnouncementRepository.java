package org.jasig.portlet.announcements.repository;

import org.jasig.portlet.announcements.model.Announcement;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AnnouncementRepository extends CrudRepository<Announcement, Long> {

    Optional<Announcement> findById(Long id);
}
