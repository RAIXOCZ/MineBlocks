package cz.raixo.blocks.util.color;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class Colors {

	public static String colorize(String string) {
		return IridiumColorAPI.process(string);
	}

	public static List<String> colorize(List<String> list) {
		return list.stream().map(Colors::colorize).collect(Collectors.toList());
	}

	public static void send(CommandSender sender, String... message) {
		sender.sendMessage(colorize(List.of(message)).toArray(String[]::new));
	}

}
