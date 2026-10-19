package com.nsu.medbook_backend.schedule.repository;

import com.nsu.medbook_backend.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByDoctorId(Long doctorId);
    List<Schedule> findByDoctorIdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);
    List<Schedule> findByDoctorIdAndStatus(Long doctorId, Schedule.ScheduleStatus status);
}
