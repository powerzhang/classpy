package com.github.zxh.classpy.classfile.bytecode;

import com.github.zxh.classpy.classfile.ClassComponent;
import com.github.zxh.classpy.classfile.ClassReader;
import java.util.ArrayList;
import java.util.List;

/*
lookupswitch
<0-3 byte pad>
defaultbyte1
defaultbyte2
defaultbyte3
defaultbyte4
npairs1
npairs2
npairs3
npairs4
match-offset pairs...
 */
public class LookupSwitch extends Instruction {

    private final List<MatchOffset> matchOffsets = new ArrayList<>();
    
    public LookupSwitch(Opcode opcode, int pc) {
        super(opcode, pc);
    }
    
    @Override
    protected void readOperands(ClassReader reader) {
        skipPadding(reader);
        
        MatchOffset defaultOffset = new MatchOffset(true, pc);
        defaultOffset.read(reader);
        
        int npairs = reader.getByteBuffer().getInt();
        for (int i = 0; i < npairs; i++) {
            MatchOffset offset = new MatchOffset(false, pc);
            offset.read(reader);
            matchOffsets.add(offset);
        }
        
        matchOffsets.add(defaultOffset);
    }
    
    private void skipPadding(ClassReader reader) {
        for (int i = 1; (pc + i) %4 != 0; i++) {
            reader.getByteBuffer().get();
        }
    }
    
    @Override
    public final List<MatchOffset> getSubComponents() {
        return matchOffsets;
    }
    
    
    public static class MatchOffset extends ClassComponent {

        private final boolean isDefault;
        private final int basePc;
        private int match;
        private int offset;

        public MatchOffset(boolean isDefault, int basePc) {
            this.isDefault = isDefault;
            this.basePc = basePc;
        }
        
        @Override
        protected void readContent(ClassReader reader) {
            if (!isDefault) {
                match = reader.getByteBuffer().getInt();
                setName(String.valueOf(match));
            } else {
                setName("default");
            }
            
            offset = reader.getByteBuffer().getInt();
            setDesc(String.valueOf(basePc + offset));
        }
        
    }
    
}