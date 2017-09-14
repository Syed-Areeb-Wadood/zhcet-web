package in.ac.amu.zhcet.controller.dean;

import com.fasterxml.jackson.annotation.JsonView;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.StudentView;
import in.ac.amu.zhcet.data.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
public class DeanRestController {

    private final ModelMapper modelMapper;
    private final StudentRepository studentRepository;

    private static Map<String, String> propertyMap = new ConcurrentHashMap<>();

    @Autowired
    public DeanRestController(ModelMapper modelMapper, StudentRepository studentRepository) {
        this.modelMapper = modelMapper;
        this.studentRepository = studentRepository;
    }

    @JsonView(DataTablesOutput.View.class)
    @PostMapping(value = "/dean/students")
    public DataTablesOutput<StudentView> getStudents(@Valid @RequestBody DataTablesInput input) {
        convertInput(input);
        return studentRepository.findAll(input, this::fromStudent);
    }

    private static void convertInput(DataTablesInput input) {
        input.getColumns().replaceAll(column -> {
            column.setData(column.getData().replace('_', '.'));
            return column;
        });
    }

    private StudentView fromStudent(Student student) {
        return modelMapper.map(student, StudentView.class);
    }

}
