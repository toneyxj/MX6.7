package com.dangdang.reader.dread.core.base;

import java.util.Hashtable;
import java.util.Map;

import com.dangdang.reader.dread.function.BaseFunction;
import com.dangdang.zframework.log.LogM;

public interface IFunctionManager {

	
	public void addFunction(String fCode, BaseFunction function);
	
	public void removeFunction(String fCode);
	
	public void removeAllFunction();
	
	public boolean doFunction(String fCode, Object...params);
	
	
	public static class FunctionManager implements IFunctionManager {
		
		private Map<String, BaseFunction> mFunctions = new Hashtable<String, BaseFunction>();
		
		public void addFunction(String fCode, BaseFunction function){
			
			if(fCode == null){
				throw new NullPointerException("[ parameter fCode cannot null ]");
			}
			if(function == null){
				throw new NullPointerException("[ parameter function cannot null ]");
			}
			mFunctions.put(fCode, function);
			
		}
		
		public void removeFunction(String fCode){
			
			if(mFunctions.containsKey(fCode)){
				mFunctions.remove(fCode);
			}
			
		}
		
		/**
		 * 执行MFunction功能 
		 */
		public boolean doFunction(String fCode, Object...params){
			
			boolean bool = false;
			BaseFunction function = mFunctions.get(fCode);
			if(function != null){
				bool = function.doFunction(params);
			} else {
				LogM.e(getClass().getSimpleName(), "[ function == null, fCode = " + fCode + " ]");
			}
			
			return bool;
			
		}
		
		public void removeAllFunction(){
			mFunctions.clear();
		}
	}
}
