package com.celskeggs.bell.vm.data;

public class DatMethod {
	public DatClass container;
	public DatString method_name;
	public int flags;
	public DatType[] parameter_types;
	public DatType return_type;
	public DatClass[] thrown_exceptions;
	public int[] line_numbers;
	public int implementation_var_count; // includes parameters
	public byte[] implementation_code;
}
