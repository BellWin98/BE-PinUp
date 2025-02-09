package com.pinup.domain.alarm.repository;

import com.pinup.domain.alarm.entity.Alarm;
import com.pinup.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Page<Alarm> findAllByReceiver(Member receiver, Pageable pageable);
}