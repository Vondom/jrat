package org.shiftone.jrat.inject.bytecode.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.SerialVersionUIDAdder;
import org.objectweb.asm.util.CheckClassAdapter;
import org.shiftone.jrat.inject.bytecode.InjectorStrategy;
import org.shiftone.jrat.inject.bytecode.TransformerOptions;

public class AsmInjectorStrategy implements InjectorStrategy {

    @Override
    public byte[] inject(byte[] rawClassData, TransformerOptions options) throws Exception {

        ClassReader reader = new ClassReader(rawClassData);
        // ClassWriter.COMPUTE_FRAMES causes weird LinkageErrors.
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor target = new CheckClassAdapter(classWriter, false);

        // target = new DebugClassVisitor(target);
        ClassInitClassVisitor classInitClassVisitor = new ClassInitClassVisitor(target);
        SerialVersionUIDAdder serialVersionUIDAdder = new SerialVersionUIDAdder(classInitClassVisitor);
        InjectClassVisitor injectClassVisitor = new InjectClassVisitor(serialVersionUIDAdder);
        MethodCriteriaClassVisitor criteriaClassVisitor = new MethodCriteriaClassVisitor(injectClassVisitor,
                serialVersionUIDAdder);
        ClassVisitor visitor = new IfInterfaceClassVisitor(target, criteriaClassVisitor);

        criteriaClassVisitor.setCriteria(options.getCriteria());
        //criteriaClassVisitor.setCriteria(MethodCriteria.ALL);

        // * IfInterfaceClassVisitor delegates to either ClassWriter or
        // InjectClassVisitor
        // this causes interface classes to be skipped (not be processed)
        // * InjectClassVisitor will column a proxy method for each method in
        // the class
        // it also adds a "jrat static init" method
        // * SerialVersionUIDAdder will add a serialVersionUID method if it is
        // missing
        // most likely this will cause a static initializer to be added
        // * ClassInitClassVisitor will add to the static initializer or add on
        // the result is the class will have one that calls the jrat static init
        // method
        reader.accept(visitor, 0);

        return classWriter.toByteArray();
    }

    @Override
    public String toString() {
        return "ASM";
    }
}
