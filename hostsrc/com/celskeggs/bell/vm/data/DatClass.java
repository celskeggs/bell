package com.celskeggs.bell.vm.data;

public final class DatClass {
	public DatSlab parent;
	public int instance_size;
	public DatString name;
	public int flags;
	public DatClass super_class;
	public DatClass[] interfaces;
	public DatMethod[] methods;
	public DatField[] fields;

	public Object assoc_rep;
}
