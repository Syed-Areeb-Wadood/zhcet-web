package amu.zhcet.core.shared.course.registration.upload;

import amu.zhcet.data.course.Course;
import amu.zhcet.data.department.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

/**
 * Shared controller between dean and department admin which allows them both to register students in a
 * particular floated course. This controller and package caters the shared functionality. For specific use
 * cases of dean and department features, please look into `dean` and `department` packages respectively
 */
@Controller
public class CourseRegistrationController {

    private final CourseRegistrationUploadService courseRegistrationUploadService;

    @Autowired
    public CourseRegistrationController(CourseRegistrationUploadService courseRegistrationUploadService) {
        this.courseRegistrationUploadService = courseRegistrationUploadService;
    }

    /**
     * Handles the uploaded CSV in Department Panel.
     * Feature is presented in Floated Course Manage Page of Department Panel
     * @param attributes RedirectAttributes to be set
     * @param department Department to which the course belongs
     * @param course Course to which the students need to be registered
     * @param file MultipartFile containing CSV listing students to be registered
     * @param session HttpSession for storing intermediate information
     * @return Layout to be rendered
     */
    @PostMapping("department/{department}/floated/{course}/register")
    public String uploadFile(RedirectAttributes attributes, @PathVariable Department department, @PathVariable Course course, @RequestParam MultipartFile file, HttpSession session) {
        if (course != null)
            courseRegistrationUploadService.upload(course, file, attributes, session);

        return "redirect:/department/{department}/floated/{course}";
    }

    /**
     * Confirms the student registration information stored in HttpSession after asking the admin.
     * Feature is present in Floated Course Manage Page of Department Panel
     * @param attributes RedirectAttributes to be set
     * @param department Department to which the course belongs
     * @param course Course to which the students need to be registered
     * @param session HttpSession containing the registration information.
     *                To be cleared after successful registration
     * @return Layout to be rendered
     */
    @PostMapping("department/{department}/floated/{course}/register/confirm")
    public String confirmRegistration(RedirectAttributes attributes, @PathVariable Department department, @PathVariable Course course, HttpSession session) {
        if (course != null)
            courseRegistrationUploadService.register(course, attributes, session);

        return "redirect:/department/{department}/floated/{course}";
    }

    /**
     * Handles the uploaded CSV in Department Panel.
     * Feature is presented in Floated Course Manage Page of Dean Admin Panel
     * @param attributes RedirectAttributes to be set
     * @param course Course to which the students need to be registered
     * @param file MultipartFile containing CSV listing students to be registered
     * @param session HttpSession for storing intermediate information
     * @return Layout to be rendered
     */
    @PostMapping("dean/floated/{course}/register")
    public String uploadFile(RedirectAttributes attributes, @PathVariable Course course, @RequestParam MultipartFile file, HttpSession session) {
        if (course != null)
            courseRegistrationUploadService.upload(course, file, attributes, session);

        return "redirect:/dean/floated/{course}";
    }

    /**
     * Confirms the student registration information stored in HttpSession after asking the admin.
     * Feature is present in Floated Course Manage Page of Dean Admin Panel
     * @param attributes RedirectAttributes to be set
     * @param course Course to which the students need to be registered
     * @param session HttpSession containing the registration information.
     *                To be cleared after successful registration
     * @return Layout to be rendered
     */
    @PostMapping("dean/floated/{course}/register/confirm")
    public String confirmRegistration(RedirectAttributes attributes, @PathVariable Course course, HttpSession session) {
        if (course != null)
            courseRegistrationUploadService.register(course, attributes, session);

        return "redirect:/dean/floated/{course}";
    }

}
