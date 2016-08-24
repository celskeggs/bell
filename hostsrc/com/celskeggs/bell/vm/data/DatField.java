package com.celskeggs.bell.vm.data;

public class DatField {
	public DatClass container;
	public DatString field_name;
	public int flags;
	public DatType field_type;
	
	public boolean has_constant_value;
	public long constant_value;
	public DatString constant_string;
}
