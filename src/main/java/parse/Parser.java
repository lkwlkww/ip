package parse;

import command.ByeCommand;
import command.Command;
import command.DeleteCommand;
import command.ListCommand;
import command.MarkCommand;
import command.TaskCommand;
import exception.DukeException;
import task.TaskList; 

/**
 * Handles the parsing of a user's inputs and the
 * appropriate responses to make.
 */
public class Parser {

  /**
   * Parses a String[] for an "event", "deadline" or "todo" task.
   *
   * @param action The command. Will be ither "event", "deadline" or "todo".
   * @param splitInput A String[] containing the user's input separated by a " " delimiter. Each separated
   * element is one element in the String[].
   *
   * @return Returns a new String[] where the first element is the command name, the
   * second element is the command description and if the command is a:
   *  - deadline: The third element is the due date.
   *  - event: The third element is the location of the event.
   * @throws DukeException Throws exceptions if the following conditions
   * are fulfilled:
   *   - The input has no task description.
   *   - The input is for a deadline or event, but it has no task and/or date description.
   *   - The input is for a deadline or event, but it has no ' /by ' or ' /at ' keyword -
   *   respectively - in the middle of its task and date description.
   */
  private static String[] parseString(String action, String[] splitInput) throws DukeException {
    String[] newSplitInput = new String[3];
    newSplitInput[0] = action;

    String[] splitInputWithoutCommand = new String[splitInput.length - 1];
    for (int i = 1; i < splitInput.length; i++) {
      splitInputWithoutCommand[i - 1] = splitInput[i];
    }

    String fullTaskDetails = String.join(" ", splitInputWithoutCommand);

    if (action.equals("todo")) {
      if (fullTaskDetails.equals("")) {
        throw new DukeException("Your todo command has no description!");
      }
      newSplitInput[1] = fullTaskDetails;

    } else {
      String dateDelimiter = "";
      if (action.equals("event")) {
        dateDelimiter = " /at ";
      } else if (action.equals("deadline")) {
        dateDelimiter = " /by ";
      }

      String[] inputArr = fullTaskDetails.split(dateDelimiter);

      if (!(inputArr.length == 2)) {
        // inputArr.length should be 2 to indicate proper formatting of the command and
        // necessary descriptions.
        throw new DukeException(
            "Your " + action + " command does not follow proper formatting!\n" +
            "A " + action + " command should follow this convention:\n" +
            action + " <task description> " + dateDelimiter + " <date description> <3"
            );
      }

      newSplitInput[1] = inputArr[0];
      newSplitInput[2] = inputArr[1];

    }
    return newSplitInput;
  }

  /**
   * Performs actions based on the input.
   * Commands:
   *   - 'Bye': Terminates the programme.
   *   - 'list': Lists the current stored tasks.
   *   - 'mark x', where x is a valid task index: Mark task x as done.
   *   - 'unmark x', where x is a valid task index: Mark task x as undone.
   *   - 'todo *', where * refers to any input: Create a Todo task.
   *   - 'event x /at y', where x and y refers to any input: Create an Event task that will happen at y.
   *   - 'deadline x /by y', where x and y refers to any input: Create a Deadline task that is due by y.
   *
   * @param input The input given by the user.
   * @param tasks The TaskList to perform appropriate actions on, after
   * parsing the input.
   * @return Returns true if the programme should continue prompting the user
   * for inputs. Returns false if the programme is to be terminated.
   * @throws DukeException When there are invalid inputs.
   */
  public static boolean settleInput(String input, TaskList tasks) throws DukeException {
    String[] splitInput = input.split(" ");
    String action = splitInput[0];
    if (action.equals("Bye")) {
      Command command = new ByeCommand(splitInput, tasks);
      return command.performAction();

    } else if (action.equals("list")) {
      // Throw error if the input contains anything other than 'list'
      if (!(splitInput.length == 1)) {
        throw new DukeException("</3 your formatting for the list command is wrong - please just type list!");
      }
      Command command = new ListCommand(splitInput, tasks);
      return command.performAction();

    } else if (
        action.equals("mark") ||
        action.equals("unmark")
        )
    {
      // Throw an error if the formatting for the 'mark' or 'unmark' command is wrong
      if (!(splitInput.length == 2)) {
        throw new DukeException(
            "Your formatting for the " + action + " command is wrong...sigh\n" +
            "In future, please do: " + action + " <index of task>\n" +
            "You can do it peepaw!"
            );
      }

      Integer index;
      // Throw an error if the character after the 'mark' or 'unmark' string is not an integer
      try {
        index = Integer.parseInt(splitInput[1]);
      } catch (NumberFormatException e) {
        throw new DukeException("Index was not properly specified (has to be an integer) for your " + action + " command!");
      }

      // Throw an error if there isn't a task with that index
      index = Integer.parseInt(splitInput[1]);
      if (index > tasks.getSize()) {
        throw new DukeException("There isn't a task with that index !!!");
      }

      Command command = new MarkCommand(splitInput, tasks);
      return command.performAction();

    } else if (
        action.equals("event") ||
        action.equals("deadline") ||
        action.equals("todo")
        )
    {
      String[] newSplitInput = parseString(action, splitInput);
      Command command = new TaskCommand(newSplitInput, tasks);
      return command.performAction();
    } else if (action.equals("delete")) {

      // Throw an error if the formatting for the 'delete' command is wrong
      if (!(splitInput.length == 2)) {
        throw new DukeException(
            "Your formatting for the " + action + " command is wrong...sigh\n" +
            "In future, please do: " + action + " <index of task>\n" +
            "You can do it peepaw!"
            );
      }

      Integer index;
      // Throw an error if the character after the 'delete' string is not an integer
      try {
        index = Integer.parseInt(splitInput[1]);
      } catch (NumberFormatException e) {
        throw new DukeException("Index was not properly specified (has to be an integer) for your " + action + " command!");
      }

      // Throw an error if there isn't a task with that index
      index = Integer.parseInt(splitInput[1]);
      if (index > tasks.getSize()) {
        throw new DukeException("There isn't a task with that index !!!");
      }

      Command command = new DeleteCommand(splitInput, tasks);
      return command.performAction();
    } else {
      throw new DukeException(
          "Your input is not recognised :(. It has to start with a command (todo, deadline, event, mark, unmark, list, Bye)"
          );
    }
  }
}
