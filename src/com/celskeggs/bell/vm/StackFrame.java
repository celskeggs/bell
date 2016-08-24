package com.celskeggs.bell.vm;

import com.celskeggs.bell.vm.data.DatMethod;

public class StackFrame {
	public StackFrame return_entry;
	public int[] locals;
	public DatMethod method;
	public int next_address;
	public int return_value_low;
	public int return_value_high;
}
