package com.celskeggs.bell.host;

import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.InstructionList;

import com.celskeggs.bell.vm.data.DatMethod;

public class BytecodeTranscoder {

	private final Packager p;
	private final Method m;
	private final DatMethod dm;

	public BytecodeTranscoder(Packager p, Method m, DatMethod dm) {
		this.p = p;
		this.m = m;
		this.dm = dm;
	}

	public void transcode(Code c) {
		dm.implementation_var_count = c.getMaxStack() + c.getMaxLocals();
		InstructionList il = new InstructionList(c.getCode());
		InstructionHandler[] handles = il.getInstructionHandles();
	}
}
