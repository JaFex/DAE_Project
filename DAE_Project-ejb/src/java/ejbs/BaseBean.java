package ejbs;
 
import dtos.DTO;
import facades.Mapper;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
 
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public abstract class BaseBean<E extends Serializable, D extends DTO, PK> implements Bean<E, D, PK> {      
   
    @PersistenceContext
    protected EntityManager em;
   
    protected Class<E> entityClass; // = getGenericClass(0);
    protected Class<D> dtoClass; // = getGenericClass(1);
 
    @Inject
    protected Mapper mapper;
   
    private static final String SQL_ALL = "SELECT entity FROM %s entity";
    private static final String SQL_UPDATE = "UPDATE %1s e SET e.%2s = :value WHERE e.%3s = :primaryKey";
    private static final String SQL_EXISTS = "SELECT COUNT(e) FROM %1$s e WHERE e.%2$s = :%2$s";    
   
    @PostConstruct
    private void init() {
        entityClass = getGenericClass(0);
        dtoClass = getGenericClass(1);
    }
   
    private <T> Class<T> getGenericClass(int index) {
        ParameterizedType genericType = (ParameterizedType) this.getClass().getGenericSuperclass();
        return (Class<T>) genericType.getActualTypeArguments()[index];
    }
   
    
    @GET
    @Override
    public List<D> all() {
        String className = entityClass.getSimpleName();
        String namedQuery = className + ".all";
       
        if (containsNamedQuery(namedQuery)) {
            return toDTOs(createNamedQuery(namedQuery).getResultList());
        }
       
        String all = String.format(SQL_ALL, className);
        return toDTOs(query(all).getResultList());
    }
   
    @POST
    @Override
    public D create(D dto) {
        E entity = toEntity(dto);
        entity = create(entity);
        return toDTO(entity);
    }
 
    @GET
    @Path("{primaryKey}")
    @Override
    public D retrieve(@PathParam("primaryKey") PK primaryKey) {
        return toDTO(find(primaryKey));
    }
   
    @PUT
    @Override
    public D update(D dto) {
        E entity = toEntity(dto);
        entity = update(entity);
        return toDTO(entity);
    }
   
    @DELETE
    @Path("{primaryKey}")
    @Override
    public boolean delete(@PathParam("primaryKey") PK primaryKey) {
        try {
            E entity = find(primaryKey);
           
            if (entity == null) {
                return false;
            }
           
            em.refresh(entity);
            em.remove(entity);
           
            return true;
        } catch (Exception e) {
            throw new EJBException(e.getMessage(), e);
        }
    }
   
    public boolean exists(E entity) {
        Field primaryKey = getPrimaryKeyField();
        String pkName = primaryKey.getName();
       
        Query query = em.createQuery(buildQueryExists(), Long.class);
        query = query.setParameter(pkName, getValue(entity, primaryKey));
       
        return (Long) query.getSingleResult() > 0;
    }
   
    private String camelToPascalCase(String value) {
        return String.valueOf(value.charAt(0)).toUpperCase() + value.substring(1);
    }
   
    private Method getMethod(String methodName, Class<?> ...args) throws NoSuchMethodException {
        try {
            return entityClass.getDeclaredMethod(methodName, args);
        } catch (NoSuchMethodException | SecurityException e) {
            return entityClass.getMethod(methodName, args);
        }
    }
   
    private <T> T getValue(E entity, Field field) {
        try {
            if (field.isAccessible()) {
                return (T) field.get(entity);
            }
           
            String fieldName = field.getName();    
            String methodName = "get" + camelToPascalCase(fieldName);
           
            Method getter = getMethod(methodName);
           
            return (T) getter.invoke(entity);
        } catch (NullPointerException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            return null;
        }
    }
   
    @Override
    public boolean exists(Object primaryKey) {
        String pkName = getPrimaryKeyField().getName();
       
        Query query = em.createQuery(buildQueryExists(), Long.class);
        query = query.setParameter(pkName, primaryKey);
       
        return (Long) query.getSingleResult() > 0;
    }
   
    protected E create(E entity) {
        if (exists(entity)) {
            throw new EntityExistsException();
        }
       
        em.persist(entity);
       
        return entity;
    }
   
    protected E update(E entity) {
        if (! exists(entity)) {
            throw new EntityNotFoundException();
        }
       
        em.merge(entity);
       
        return entity;
    }
   
    public D createOrUpdate(D dto) {
        return toDTO(createOrUpdate(toEntity(dto)));
    }
   
    protected E createOrUpdate(E entity) {
        return exists(entity) ? update(entity) : create(entity);
    }
   
    private <A extends Annotation> Stream<A> getAnnotations(Class<A> annotationClass) {
        return Arrays.stream(entityClass.getAnnotationsByType(annotationClass));
    }
   
    private Stream<NamedQuery> getNamedQueries() {
        // 3. Get the @NamedQuery annotations of E
        Stream<NamedQuery> nq = getAnnotations(NamedQuery.class);
       
        // 4. check also for @NamedQueries annotations.
        Stream<NamedQuery> nq2 = getAnnotations(NamedQueries.class).map(NamedQueries::value).flatMap(Arrays::stream);
       
        return Stream.concat(nq, nq2);
    }
   
    private boolean containsNamedQuery(String namedQuery) {
        Stream<NamedQuery> namedQueries = getNamedQueries();
        return namedQueries.map(NamedQuery::name).anyMatch(namedQuery::equals);
    }
       
    public E find(Object primaryKey) {
        return em.find(entityClass, primaryKey);
    }
   
    public E findOrFail(Object primaryKey) {
        E entity = find(primaryKey);
       
        if (entity == null) {
            String entityName = entityClass.getSimpleName();
            String pkName = getPrimaryKeyField().getName();
            throw new EntityNotFoundException("Record not found in " + entityName + " for " + pkName + " = " + primaryKey);    
        }
       
        return entity;
    }
           
    private String buildQueryExists() {
        String entityName = entityClass.getSimpleName();
        String pkName = getPrimaryKeyField().getName();
       
        return String.format(SQL_EXISTS, entityName, pkName);
    }
           
    public E toEntity(D dto) {
        return mapper.map(dto, entityClass);
    }
       
    public List<E> toEntities(List<D> dtos) {
        return mapper.map(dtos, entityClass);
    }
   
    public D toDTO(E entity) {
        return mapper.map(entity, dtoClass);
    }
   
    public List<D> toDTOs(List<E> entities) {
        return mapper.map(entities, dtoClass);
    }
       
    private <T> List<Field> getAllDeclaredFields(Class<T> tClass) {
        List<Field> fields = new LinkedList<>();
       
        Class<? super T> parentClass = tClass.getSuperclass();
       
        if (parentClass != null) {
            fields.addAll(getAllDeclaredFields(parentClass));
        }
       
        fields.addAll(Arrays.asList(tClass.getDeclaredFields()));
       
        return fields;
    }
   
    private Field getPrimaryKeyField() {
        List<Field> fields = getAllDeclaredFields(entityClass);
        Predicate<Field> isPrimaryKey = field -> field.getAnnotationsByType(Id.class).length > 0;
       
        return fields.stream().filter(isPrimaryKey).findFirst().get();
    }
   
    public Query createNamedQuery(String name) {
        return em.createNamedQuery(name, entityClass);
    }
   
    public Query query(String qlString) {
        return em.createQuery(qlString, entityClass);
    }
}