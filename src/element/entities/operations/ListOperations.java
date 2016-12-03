package element.entities.operations;

import java.util.ArrayList;

import element.ElemTypes;
import element.entities.number.Numeric;

public class ListOperations {

	public static void removeIndices(ArrayList<Object> list, ArrayList<Numeric> indices) {
		int size = list.size();
		
		for (int i = 0; i < indices.size(); i++) {
			list.set(indices.get(i).toIndex(size), null);
		}
		
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == null) {
				list.remove(i);
				i--;
			}
		}
		
	}
	
	public static void removeObjects(ArrayList<Object> list, ArrayList<Object> objects) {		
		for (Object key : objects) {
			for (int i = 0; i < list.size(); i++) {
				if (ElemTypes.areEqual(list.get(i), key)) {
					list.remove(i);
					i--;
				}
			}
		}
	}

}
