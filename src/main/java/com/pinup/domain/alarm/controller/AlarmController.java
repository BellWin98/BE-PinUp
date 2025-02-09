package com.pinup.domain.alarm.controller;

import com.pinup.domain.alarm.dto.response.AlarmResponse;
import com.pinup.global.response.ResultCode;
import com.pinup.global.response.ResultResponse;
import com.pinup.domain.alarm.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping
    public ResponseEntity<ResultResponse> getMyAlarms(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AlarmResponse> result = alarmService.getMyAlarms(pageable);

        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_ALARMS_SUCCESS, result));
    }

    @GetMapping("/{alarmId}")
    public ResponseEntity<ResultResponse> getAlarm(@PathVariable Long alarmId) {

        AlarmResponse result = alarmService.getAlarm(alarmId);

        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_ALARM_DETAIL_SUCCESS, result));
    }
}
