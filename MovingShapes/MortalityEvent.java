public class MortalityEvent {
    LivingThing source;
    boolean isDeathEvent;

    public MortalityEvent(LivingThing source, boolean isDeathEvent){
        this.source = source;
        this.isDeathEvent = isDeathEvent;
    }

    public LivingThing getSource(){
        return source;
    }
}
