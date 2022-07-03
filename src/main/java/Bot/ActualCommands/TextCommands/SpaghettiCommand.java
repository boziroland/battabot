package Bot.ActualCommands.TextCommands;

import Bot.CommandManagement.ICommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SpaghettiCommand implements ICommand {
    @Override
    public String command() {
        return "spaghetti";
    }

    @Override
    public String help() {
        return "Sends the the recipe of the critically acclaimed Fülep spaghetti (in hungarian).";
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {

        event.getChannel().sendMessage("Hozzávalók: 40-50dkg trapista sajt 4-5db vörös hagyma, 60dkg darált sertés lapocka, 2 kiskanál őrölt pirospaprika, 3-4 csipetnyi őrölt bors, 2kiskanál oregánó, 1 evőkanálnyi ketchup  1 evőkanálnyi kristálycukor, 1csomag bolonyai mártás alap por , 1 csomag (500g) spagetti tészta\n[A tésztát nagy lángon főzzétek ki] A tésztát bő vízben 1evőkanál sóval fél deci olajjal kifőzni, ha kifőtt átszűrni hidegvízzel 3x, majd félre tenni lecsepegni\n\n [Kis lángon főzzétek!!!]\n\n A hagymákat apró kockára fel kell vágni, egy edénybe(amibe a levest főzöd) olajon odatenni üvegesre puhítani Amikor ez kész rá kell tenni a darálthúst, a pirospaprikát, az őrölt borsot ízlés szerinti sót, a cukrot, majd át kell kavarni és annyi vízzel felönteni, hogy ellepje. (Nem úgy mintha tésztát főznél, csak épp hogy ellepje)\n\n" +
                "Amikor a hús félpuha az oregánót is rá kell tenni a ketchuppal együtt és ha szükséges a vizet pótolni kell hogy tovább tudjon főni. Ezt azért néha kavargatni kell mert leéghet.\nAmikor a hús már puha akkor bele kavarjuk a Bolognai mártás alapport. (Folyomatos szórás közben kell kavargatni)  Innentől egy 5 perc az átfőzés és kész a hús.Ha a tészta teljesen lecsepegett, akkor már össze lehet keverni").queue();

    }
}
