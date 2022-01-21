package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.*;
import uz.pdp.appjparelationships.payload.StudentDTO;
import uz.pdp.appjparelationships.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    UniversityRepository universityRepository;

    @Autowired
    FacultyRepository facultyRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    SubjectRepository subjectRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 5);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 5);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping("/byFaculty/{faculty_id}")
    public Page<Student> getStudentsByFaculty(@PathVariable Integer faculty_id, @RequestParam int page) {
        Optional<Faculty> optionalFaculty = facultyRepository.findById(faculty_id);
        if (optionalFaculty.isPresent()) {
            Pageable pageable = PageRequest.of(page, 5);
            Page<Student> studentPage = studentRepository.findAllByGroup_FacultyId(faculty_id, pageable);
            return studentPage;
        }
        return null;
    }

    //4. GROUP OWNER
    @GetMapping("/byGroup/{group_id}")
    public Page<Student> getStudentsByGroupId(@PathVariable Integer group_id, @RequestParam int page) {
        Optional<Group> optionalGroup = groupRepository.findById(group_id);
        if (optionalGroup.isPresent()) {
            Pageable pageable = PageRequest.of(page, 5);
            Page<Student> studentPage = studentRepository.findAllByGroupId(group_id, pageable);
            return studentPage;
        }
        return null;
    }

    @PostMapping
    public String addStudent(@RequestBody StudentDTO studentDTO) {
        Student student = new Student();
        String firstName = studentDTO.getFirstName();
        String lastName = studentDTO.getLastName();
        Integer group_id = studentDTO.getGroup_id();
        Integer address_id = studentDTO.getAddress_id();
        List<Integer> subjects_ids = studentDTO.getSubjects_ids();

        Optional<Address> optionalAddress = addressRepository.findById(address_id);
        if (!optionalAddress.isPresent()) {
            return "Address not found";
        }
        Address address = optionalAddress.get();

        Optional<Group> optionalGroup = groupRepository.findById(group_id);
        if (!optionalGroup.isPresent()) {
            return "Group not found";
        }
        Group group = optionalGroup.get();

        if (studentRepository.existsByFirstNameAndLastNameAndAddress_Id(firstName, lastName, address_id)) {
            return "Student already exists";
        }
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setGroup(group);
        student.setAddress(address);
        List<Subject> subjects = new ArrayList<>();
        for (Integer subjects_id : subjects_ids) {
            Optional<Subject> optionalSubject = subjectRepository.findById(subjects_id);
            if (optionalSubject.isPresent()) {
                subjects.add(optionalSubject.get());
            }
            return "Subject not found";
        }
        student.setSubjects(subjects);
        studentRepository.save(student);
        return "Student saved";
    }

    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Integer id){
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if(optionalStudent.isPresent()){
            studentRepository.delete(optionalStudent.get());
            return "Student deleted";
        }
        return "Student not found";
    }

    @PutMapping("/{id}")
    public String editStudent(@PathVariable Integer id,@RequestBody StudentDTO studentDTO){
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if(optionalStudent.isPresent()){
            Student editStudent = optionalStudent.get();
            String firstName = studentDTO.getFirstName();
            String lastName = studentDTO.getLastName();
            Integer address_id = studentDTO.getAddress_id();
            Integer group_id = studentDTO.getGroup_id();
            List<Subject> subjects=new ArrayList<>();

            Optional<Address> optionalAddress = addressRepository.findById(address_id);
            if(!optionalAddress.isPresent()){
                return "Address not found";
            }
            Address address = optionalAddress.get();

            Optional<Group> optionalGroup = groupRepository.findById(group_id);
            if(!optionalGroup.isPresent()){
                return "Group not found";
            }
            Group group = optionalGroup.get();

            if(studentRepository.existsByIdIsNotAndFirstNameAndLastNameAndAddress_Id(id,firstName,lastName,studentDTO.getAddress_id())){
                return "Student already exists";
            }

            for (Integer subjects_id : studentDTO.getSubjects_ids()) {
                Optional<Subject> optionalSubject = subjectRepository.findById(subjects_id);
                if(!optionalSubject.isPresent()){
                    return "Subject not found";
                }
                subjects.add(optionalSubject.get());
            }

            editStudent.setSubjects(subjects);
            editStudent.setFirstName(firstName);
            editStudent.setLastName(lastName);
            editStudent.setAddress(address);
            editStudent.setGroup(group);
            studentRepository.save(editStudent);
            return "Student edited";
        }
        return "Student not found";
    }
}
