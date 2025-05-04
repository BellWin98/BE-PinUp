package com.pinup.domain.alarm.service;

import com.pinup.domain.alarm.dto.response.AlarmResponse;
import com.pinup.domain.alarm.entity.Alarm;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.alarm.exception.UnauthorizedAlarmAccessException;
import com.pinup.global.common.AuthUtil;
import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.response.ErrorCode;
import com.pinup.domain.alarm.repository.AlarmRepository;
import com.pinup.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final AuthUtil authUtil;
    private final MemberRepository memberRepository;

    public Page<AlarmResponse> getMyAlarms(Pageable pageable) {
        Member currentMember = authUtil.getLoginMember();
        return alarmRepository.findAllByReceiver(currentMember, pageable)
                .map(AlarmResponse::from);
    }

    public AlarmResponse getAlarm(Long alarmId) {
        Member currentMember = authUtil.getLoginMember();
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ALARM_NOT_FOUND));

        if (!alarm.getReceiver().equals(currentMember)) {
            throw new UnauthorizedAlarmAccessException();
        }
        alarm.read();
        alarmRepository.save(alarm);
        return AlarmResponse.from(alarm);
    }
}