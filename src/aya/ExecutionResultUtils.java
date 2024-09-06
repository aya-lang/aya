package aya;

import java.util.ArrayList;

import aya.obj.Obj;

public class ExecutionResultUtils {
	public static ArrayList<Obj> getDataOrThrowIfException(ExecutionResult result) {

		if (result.getType() == ExecutionResult.TYPE_SUCCESS) {
			return ((ExecutionResultSuccess)result).getData();
		} else {
			throw ((ExecutionResultException)result).ex();
		}
		
	}
}
