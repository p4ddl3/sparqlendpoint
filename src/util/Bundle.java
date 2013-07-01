package util;



import java.util.HashMap;
import java.util.Map;
/*
 * Merci de ne pas toucher a cette classe ! C'est du version Alpha en puissance lol :)
 */

public class Bundle {
	
	private Map<String, Object> bundle;
	private String name;
	public Bundle(String name){
		this.name = name;
		bundle = new HashMap<String,Object>();
	}

	public boolean isNamedAs(String possibleName){
		return name ==possibleName;
	}
	public Bundle put(String key, Object value){
		bundle.put(key,value);
		return this;
	}
	public Object getValue(String key){
		try{
			if(!bundle.containsKey(key))
				throw new UndefinedValueBundleException(name +" : '" + key + "' key doesn't exist.");
		}catch(UndefinedValueBundleException uvbe){
			uvbe.printStackTrace();
		}
		return bundle.get(key);
	}
	public Bundle clear(){
		bundle.clear();
		return this;
	}
	
	/*
	 * Oups, je crois que j'ai un peu touch� ! 
	 * Pas sur de la l�gitimit� et de l'utilit� du static ici, je l'ai pas mis dans le doute
	 */
	public class UndefinedValueBundleException extends Exception{
		private static final long serialVersionUID = 1L;
		public UndefinedValueBundleException(){
			super();
		}
		public UndefinedValueBundleException(String message){
			super(message);
		}
	}
	/*
	 *
	 *                                              ______
          _                 _ __...----~~~~~ _
         //   ___...--- ~~~ \\       .      //  \|/
 __..--_// ~~~        .     _\\   .         \\_     .
      //\\\    \|/.        //\\\           ///\\ .
     //-\-\\              //-/-\\         //-/-\\
    //\_-_/\\            //\_-_/\\    .  //\_-_/\\
.    /\ @ /\              /\\_//\         /\\_//\     .
    /\ \_/ /\            /\ \O/ /\       /\'\=/'/\
   /__\_\_/__\     .    /__\_\_/__\     /__\_\_/__\
  /___________\        /___________\   /___________\  .
 (   _____/:_) )      (  _______/:_)) ((_:\_______  )
  \_(_:\______/        \_(_:\______/   \______/:_)_/   \|/
                        (
  .                      )  /( ( (  .    .
           /\   .       )\ )\  /(         /\    /\
          /=/           \(/ _\/ /(        \=\  /=/   .
   .     /=/___       ()__)/ /(__()   .    \=\/=/
        /=/////\\         (_)             __\=|/
        \_\////_(_                       ////(_)\
        //////   _\  elle est           _)_\\\\\\\
        \///(.  _\    bonne ton        /_   .)\\\\\
 .       (:) | _\         herbe_      __/___o/\\\\\\_ .
      ___(:) ' \___ .__/\\\___/_/    /         \\\\\\\
     /   (:)       \   \' /         /(*)(*)(*)(*\\\\\\\
    /  _ (:)     _  \  / /          | _  _  _  _ \\\\\\
   /  / \__   __/ \  \/ /           |/_\/_\/_\/_\/_\/_|
   (  \ |       |__\   /   .         \_/\_/\_/\_/\_/\/
    \  \|       |   \_/              |/_\/_\/_\/_\/_\|
     \  |_______|/   \               |\_/\_/\_/\_/\_/|
.     \/         \   /         .    /              jro\ .
      /           \ /              /(*)(*)(*)(*)(*)(*)(\
      \___________//   .           \___________________/
   .                       \|/ .             .            .

	 * 
	 */
}
