package in.ac.amu.zhcet.service.core;

import in.ac.amu.zhcet.data.model.CourseInCharge;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.repository.CourseInChargeRepository;
import in.ac.amu.zhcet.data.repository.FloatedCourseRepository;
import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CourseInChargeService {

    private final FacultyService facultyService;
    private final FloatedCourseRepository floatedCourseRepository;
    private final CourseManagementService courseManagementService;
    private final CourseInChargeRepository courseInChargeRepository;

    @Autowired
    public CourseInChargeService(FacultyService facultyService, FloatedCourseRepository floatedCourseRepository, CourseManagementService courseManagementService, CourseInChargeRepository courseInChargeRepository) {
        this.facultyService = facultyService;
        this.floatedCourseRepository = floatedCourseRepository;
        this.courseManagementService = courseManagementService;
        this.courseInChargeRepository = courseInChargeRepository;
    }

    @Transactional
    public void setInCharge(String courseId, List<CourseInCharge> courseInCharges) {
        if (courseInCharges == null)
            return;

        FloatedCourse stored = courseManagementService.getFloatedCourseByCode(courseId);
        if (stored == null)
            return;

        for (CourseInCharge inCharge : stored.getInCharge()) {
            if (!courseInCharges.contains(inCharge))
                courseInChargeRepository.delete(inCharge.getId());
        }

        stored.getInCharge().clear();

        for (CourseInCharge courseInCharge : courseInCharges)
            addInCharge(stored, courseInCharge.getFacultyMember().getFacultyId(), Utils.nullIfEmpty(courseInCharge.getSection()));
    }

    private void addInCharge(FloatedCourse stored, String facultyId, String section) {
        FacultyMember facultyMember = facultyService.getById(facultyId);
        if (facultyMember == null) {
            log.error("No such faculty member : %s", facultyId);
            return;
        }

        CourseInCharge inCharge = courseInChargeRepository.findByFloatedCourseAndFacultyMemberAndSection(stored, facultyMember, section);
        if (inCharge != null) {
            log.error("No such in charge : %s %s %s", stored.getCourse().getCode(), facultyMember.getFacultyId(), section);
            return;
        }

        CourseInCharge courseInCharge = new CourseInCharge();
        courseInCharge.setFacultyMember(facultyMember);
        courseInCharge.setFloatedCourse(stored);
        courseInCharge.setSection(section);

        stored.getInCharge().add(courseInCharge);
        floatedCourseRepository.save(stored);
    }

    public List<CourseInCharge> getCourseByFaculty(FacultyMember facultyMember) {
        return courseInChargeRepository.findByFacultyMemberAndFloatedCourse_Session(facultyMember, ConfigurationService.getDefaultSessionCode());
    }

    public CourseInCharge getCourseInCharge(String floatedCourseCode, String section) {
        return courseInChargeRepository.findByFacultyMemberAndFloatedCourse_Course_CodeAndSection
                (facultyService.getLoggedInMember(), floatedCourseCode, Utils.nullIfEmpty(section));
    }

    public CourseInCharge getCourseInChargeAndVerify(String floatedCourseCode, String section) {
        CourseInCharge courseInCharge = getCourseInCharge(floatedCourseCode, section);
        if (courseInCharge == null) {
            log.error("Forced Access of Course In Charge %s %s", floatedCourseCode, section);
            throw new AccessDeniedException("403");
        }

        return courseInCharge;
    }

    public List<CourseRegistration> getCourseRegistrations(CourseInCharge courseInCharge) {
        String section = Utils.nullIfEmpty(courseInCharge.getSection());
        if (section == null) // Allow all registrations
            return courseInCharge.getFloatedCourse().getCourseRegistrations();
        else
            return courseInCharge.getFloatedCourse().getCourseRegistrations()
                .stream()
                .filter(courseRegistration -> courseRegistration.getStudent().getSection().equals(section))
                .collect(Collectors.toList());
    }

    public Set<String> getSections(FloatedCourse floatedCourse) {
        if (floatedCourse == null)
            return Collections.emptySet();

        return floatedCourse.getCourseRegistrations().stream()
                .map(courseRegistration -> courseRegistration.getStudent().getSection())
                .collect(Collectors.toSet());
    }

    public Set<String> getSections(String courseId) {
        return getSections(courseManagementService.getFloatedCourseByCode(courseId));
    }

    public boolean isInCharge(List<CourseInCharge> courseInCharges, FacultyMember member) {
        return courseInCharges
                .stream()
                .map(CourseInCharge::getFacultyMember)
                .collect(Collectors.toList())
                .contains(member);
    }

}
