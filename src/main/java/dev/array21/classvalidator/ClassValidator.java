package dev.array21.classvalidator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import dev.array21.classvalidator.annotations.External;
import dev.array21.classvalidator.annotations.Required;

/**
 * Utility to validate if a Class matches the required specification
 * @author Tobias de Bruijn
 * @since 1.0.0
 */
public class ClassValidator {

	/**
	 * Validate an Object to it's class<br>
	 * Required fields should be annotated with {@link Required}
	 * @param <T> Type of input
	 * @param input The input
	 * @return Returns a Pair where:<br>
     *		- If A is null, an exception occurred. Description contained in B.<br>
 	 * 		- If A is false, the object did not pass validation. Description for the user contained in B<br>
 	 * 		- if A is true, the validation passed. B will be null
	 */
	public static <T> Pair<Boolean, String> validateType(T input) {
		List<Field> fields = new ArrayList<>(Arrays.asList(input.getClass().getDeclaredFields()));
		
		Class<?> superClass = input.getClass().getSuperclass();
		if(superClass != null) {
			Class<?> superClazz = superClass;
			while(superClazz.getSuperclass() != null) {
				fields.addAll(Arrays.asList(superClazz.getDeclaredFields()));
				superClazz = superClazz.getSuperclass();
			}
		}
		
		List<Class<?>> internalClasses = Arrays.asList(input.getClass().getDeclaredClasses());
		for(Field f : fields) {
			if(f.isAnnotationPresent(Required.class)) {
				try {
					f.setAccessible(true);
					if(f.get(input) == null) {
						return new Pair<Boolean, String>(false, "Missing required field: " + f.getName());
					}
					
					if(f.getType().isArray() && (internalClasses.contains(f.getType().getComponentType()) || f.isAnnotationPresent(External.class))) {
						Object[] oArr = (Object[]) f.get(input);
						
						inner: for(int j = 0; j < oArr.length; j++) {
							Pair<Boolean, String> innerValidation = validateType(oArr[j]);
							if(innerValidation.getA() != null && innerValidation.getA() == true) {
								continue inner;
							} else {
								if(innerValidation.getA() == null) {
									return innerValidation;
								} else {
									List<String> reason = Arrays.asList(innerValidation.getB().split(Pattern.quote(" ")));
									
									return new Pair<Boolean, String>(false, String.format("%s %s.%d.%s", String.join(" ", reason.subList(0, reason.size() -1)), f.getName(), j, reason.get(reason.size() -1)));
								}
							}
						}
					}
					
					if(internalClasses.contains(f.getType()) || f.isAnnotationPresent(External.class)) {
						Pair<Boolean, String> innerValidation = validateType(f.get(input));
						if(innerValidation.getA() != null && innerValidation.getA() == true) {
							continue;
						} else {
							if(innerValidation.getA() == null) {
								return innerValidation;
							} else {
								List<String> reason = Arrays.asList(innerValidation.getB().split(Pattern.quote(" ")));
								
								return new Pair<Boolean, String>(false, String.format("%s %s.%s", String.join(" ", reason.subList(0, reason.size() -1)), f.getName(), reason.get(reason.size() -1)));
							}
						}	
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					return new Pair<Boolean, String>(null, "Failed to validate Type " + input.getClass().getName());
				}
			}
		}

		return new Pair<Boolean, String>(true, null);
	}
}
