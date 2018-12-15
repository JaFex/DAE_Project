package facades;

import java.util.LinkedList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

@Default
@ApplicationScoped
public class Mapper extends ModelMapper {
    
    @PostConstruct
    private void init() {
        getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        
        // createTypeMap(Student.class, StudentDTO.class);
        // createTypeMap(StudentDTO.class, Student.class);
        
        // createTypeMap(Course.class, CourseDTO.class);
        // createTypeMap(CourseDTO.class, Course.class);
        
        // createTypeMap(Subject.class, SubjectDTO.class);
        // createTypeMap(SubjectDTO.class, Subject.class);
        
        // createTypeMap(Teacher.class, TeacherDTO.class);
        // createTypeMap(TeacherDTO.class, Teacher.class);
        
        // validate();
    }
        
    public <S, D> List<D> map(List<S> source, Class<D> destinationClass) {
        List<D> destination = new LinkedList<>();
        source.forEach(s -> destination.add(map(s, destinationClass)));
        return destination;
    }
}
