package com.esteban.equipos.mongo.controller;
import com.esteban.equipos.mongo.service.ReglaNegocio;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.*;
import org.springframework.dao.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.transaction.TransactionException;
import java.util.stream.Collectors;
@RestControllerAdvice(basePackages="com.esteban.equipos.mongo.controller", annotations=RestController.class)
public class ApiErrores {
 private ResponseEntity<ProblemDetail> error(int status,String detalle){
  ProblemDetail p=ProblemDetail.forStatusAndDetail(HttpStatus.valueOf(status),detalle);return ResponseEntity.status(status).body(p);
 }
 @ExceptionHandler(ReglaNegocio.class) ResponseEntity<ProblemDetail> negocio(ReglaNegocio e){return error(e.getEstado(),e.getMessage());}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ProblemDetail> validacion(MethodArgumentNotValidException e){
  return error(400,e.getBindingResult().getFieldErrors().stream().map(f->f.getField()+": "+f.getDefaultMessage()).collect(Collectors.joining("; ")));
 }
 @ExceptionHandler({HttpMessageNotReadableException.class,MethodArgumentTypeMismatchException.class}) ResponseEntity<ProblemDetail> formato(Exception e){return error(400,"Revisa los tipos de datos y el formato JSON. Usa fechas AAAA-MM-DD e IDs numéricos.");}
 @ExceptionHandler({DuplicateKeyException.class,TransientDataAccessException.class,TransactionException.class}) ResponseEntity<ProblemDetail> conflicto(Exception e){return error(409,"Conflicto al guardar. Actualiza los datos y vuelve a intentarlo.");}
 @ExceptionHandler(DataAccessException.class) ResponseEntity<ProblemDetail> conexion(Exception e){return error(503,"No fue posible acceder a MongoDB. Revisa la conexión y vuelve a intentarlo.");}
}
