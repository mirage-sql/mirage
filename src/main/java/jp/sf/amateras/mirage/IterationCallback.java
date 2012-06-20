package jp.sf.amateras.mirage;

/**
 * Callback interface for iteration search.
 * 
 * @author Naoki Takezoe
 *
 * @param <T> the entity type
 * @param <R> the return type
 * 
 * @see SqlManager#iterate(Class, IterationCallback, String)
 * @see SqlManager#iterate(Class, IterationCallback, String, Object)
 */
public interface IterationCallback<T, R> {

	public R iterate(T entity);

}
