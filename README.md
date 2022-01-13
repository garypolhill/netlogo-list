# netlogo-list
A mutable list for NetLogo created in the hope of avoiding garbage collection errors for models doing a lot of list processing. NetLogo's lists are immutable -- once created they cannot change, and effectively, you create a different list when you add items using commands like `fput` and `lput`. If you do this with a command like `set my-list lput an-item my-list`, then the old `my-list` (without `an-item` added) is destroyed, and the garbage collector in the Java Virtual Machine has to find it and free the memory. If you do this enough, you will find the garbage collector running to much -- and when it reaches 98% of the execution effort of the program, Java will fail with an `OutOfMemoryException`. This is a problem you cannot fix by giving Java/NetLogo more RAM!

These problems aside, a mutable list is a useful data structure in its own right. It is generally there when you want an ordered collection of items where typically, you only care about the first item on the list and the rest. Adding things to the end of the list will be slower in comparison, because doing so entails iterating to the end of the list. A future implementation _may_ make this faster, but this implementation is a list 'purist' and does not provide 'random' access to elements of the list (beyond, for convenience, the tenth element), so there is no equivalent of the `item` command. To allow you to edit the list at points other than the front, you can get a `cursor` which you can move up and down the list, and `insert`, `overwrite` and `delete` at the cursor.

The following summarizes the commands implemented. Where a _list_ is stipulated as an argument, this must be a mutable list unless otherwise stated.

## Construction commands

  + `lt:copy` _list_ : return a list created by copying the _list_. Modifications to the copy will not affect the _list_, and vice versa.
  + `lt:from-agentset` _agentset_ : return a list containing each of the turtles, patches or links in _agentset_, in arbitrary order.
  + `lt:from-list` _logolist_ : return a mutable list constructed from the NetLogo list argument _logolist_.
  + `lt:make` : return a new, empty mutable list.
  + `lt:range` _start_ _stop_ _increment_ : return a list of numbers starting at _start_, incrementing by _increment_ (optional, default `1` if _start_ < _stop_, and -1 otherwise) until _stop_ is reached. The list will not contain any number larger than _stop_ (if it is more than _start_; smaller than _stop_ otherwise).
  + `lt:cursor` _list_ : return a cursor for the list, starting at the front.
  + `lt:map` _reporter_ _list_ ; `(lt:map` _reporter_ _list1_ _list2_ _list3_ ... `)` : run the _reporter_ on each element of _list_ and return a mutable list containing the result. In the multi-argument form, the list returned will have the same length as the shortest _list_ in the arguments. (This is a bit more forgiving than NetLogo's `map` command, which stipulates that all lists must have the same length.)

## Access commands

 + `lt:foreach` _list_ _command-block_ : execute the _command-block_ for each item in the _list_.
 + `lt:foreach-depth-first` _list_ _command-block_ : execute the _command-block_ for each item in the _list_ recursively iterating any elements of _list_ that are mutable lists. 
 + `lt:left` _cursor_ : move the cursor one element to the left, returning the item stored in its list there.
 + `lt:right` _cursor_ : move the cursor one element to the right, returning the item stored in its list there.

The following commands provide direct access to the named element of the list, and will cause an error if there is no such element.

  + `lt:first` _list_ 
  + `lt:second` _list_
  + `lt:third` _list_
  + `lt:fourth` _list_
  + `lt:fifth` _list_
  + `lt:sixth` _list_
  + `lt:seventh` _list_
  + `lt:eighth` _list_
  + `lt:ninth` _list_
  + `lt:tenth` _list_

## Modification commands

  + `lt:cat` _list_ _other_ ; `(lt:cat` _list_ _other1_ _other2_ _other3_ ... `)` : add the _other_ mutable list to the end of _list_ -- _other_ will be emptied
  + `lt:clear` _list_ : empty the list; it will now have length zero.
  + `lt:delete` _cursor_ : delete the entry at the current cursor position. You must then call `lt:left` or `lt:right` before deleting again.
  + `lt:filter` _boolean-reporter_ _list_ : remove items from _list_ for which the _boolean-reporter_ is _false_.
  + `lt:fpop` _list_ : remove the first element of the list and report it
  + `ls:fpush` _list_ _item_ ; `(lt:fpush` _list_ _item1_ _item2_ _item3_ ... `)` : add _item_ to the front of the list. In the multi-argument form, the order of the items will be preserved, leading to a different resulting list than `lt:fpush`ing the _items_ individually.
  + `lt:fpush-all` _list_ _collection_ : add all the elements in the _collection_ (which may be a mutable list, a NetLogo list, or an AgentSet) to the _list_.
  + `lt:insert` _cursor_ _item_ : add an item in the _cursor_'s list before the cursor.
  + `lt:keep` _list_ _item_ ; `(lt:keep` _list_ _item1_ _item2_ _item3_ ... `)` : remove everything from the _list_ that isn't one of the _items_ in the arguments.
  + `lt:lpop` _list_ : remove the last element from the list and report it (this will be slower than `fpop`).
  + `lt:lpush` _list_ _item_ ; `(lt:lpush` _list_ _item1_ _item2_ _item3_ ... `)` : add the _items_ to the end of the list (will be slower than `fpush`).
  + `lt:lpush-all` _list_ _collection_ : add all the elements in the _collection_ (which may be a mutable list, a NetLogo list, or an AgentSet) to the _list_.
  + `lt:overwrite` _cursor_ _item_ : replace whatever is stored at the _cursor_'s list where the _cursor_ currently is with the _item_. You can only do this once before calling `lt:left` or `lt:right` again.
  + `lt:remove` _list_ _item_ ; `(lt:remove` _list_ _item1_ _item2_ _item3_ ... `)` : remove every occurrence of the _items_ from the _list_, if they occur at all.
  + `lt:remove-duplicates` _list_ : ensure each element of the _list_ is unique.
  + `lt:remove-once` _list_ _item_ : remove the _item_ from the _list_ once, if it occurs at all.
  + `lt:reverse` _list_ : reverse the order of the elements in the list.
  + `lt:shuffle` _list_ : reorder the list in random order.
  + `lt:sort` _list_ _number-block_ : sort the _list_ using the _number-block_ which must take two arguments, and return a number less than zero if the first argument is to come before the second in the resulting list, more than zero if it is to come after, and equal to zero if their relative order is unimportant.

The following commands allow you to directly change the _item_ stored at the named element in the _list_:

  + `lt:set-first` _list_ _item_
  + `lt:set-second` _list_ _item_
  + `lt:set-third` _list_ _item_
  + `lt:set-fourth` _list_ _item_
  + `lt:set-fifth` _list_ _item_
  + `lt:set-sixth` _list_ _item_
  + `lt:set-seventh` _list_ _item_
  + `lt:set-eighth` _list_ _item_
  + `lt:set-ninth` _list_ _item_
  + `lt:set-tenth` _list_ _item_

## Information commands

  + `lt:counts` _list_ : returns a NetLogo list, each element of which is another NetLogo list containing an item and the number of times it appears in the modifiable list. These will not necessarily be in the same order as the items appear in the modifiable list.
  + `lt:deep-member?` _list_ _item_ : recursively searches _list_ and any mutable-list elements in it for _item_, returning _true_ if _item_ is found.
  + `lt:empty?` _list_ : returns _true_ iff _list_ has length 0
  + `lt:has-left?` _cursor_ : can you call `lt:left` without getting an error message? i.e. Is the _cursor_ at the front of its list?
  + `lt:has-right?` _cursor_ : can you call `lt:right` without getting an error message? i.e. Is the _cursor_ at the end of its list?
  + `lt:histogram` _list_ _min_ _max_ _width_ : returns a histogram of the numerical elements in the _list_ (other elements are ignored), in the form of a NetLogo list of counts. There will be (_max_ - _min_) / _width_ elements in the NetLogo list returned. Numbers less than _min_ or more than or equal to _max_ are ignored. Note that this won't affect any plots, unlike NetLogo's `histogram` command.
  + `lt:intersects?` _list_ _collection_ : returns _true_ if any of the elements in the _collection_ (which may be a mutable list, a NetLogo list, or a mutable list) appear in the _list_.
  + `lt:is-cursor?` _thing_ : returns _true_ if the _thing_ is a cursor of a mutable list
  + `lt:is-list?` _thing_ : returns _true_ if the _thing_ is a mutable list
  + `lt:length` _list_ : return the length of the list
  + `lt:max` _list_ : return the maximum number stored in the _list_; if there are no numbers in the list, the result is `Double.NaN`. You can test for this with `(word` _variable_ `) = "NaN"`.
  + `lt:mean` _list_ : return the mean of the numbers stored in the _list_; if there are no numbers in the list, the result is `Double.NaN`. You can test for this with `(word` _variable_ `) = "NaN"`.
  + `lt:median` _list_ : return the median of the numbers stored in the _list_; if there are no numbers in the list, the result is `Double.NaN`. You can test for this with `(word` _variable_ `) = "NaN"`.
  + `lt:member?` _list_ _item_ ; `(lt:member?` _list_ _item1_ _item2_ _item3_ ... `)` : check if all of the _items_ are in the _list_. Note the arguments are not in the same order as NetLogo's `member` command, to allow the variadic syntax, which will be more efficient than `lt:member?` _list_ _item1_ `and lt:member?` _list_ _item2_ `and lt:member?` _list_ _item3_ `and` ... because the _list_ is only searched once rather than however many times.
  + `(lt:member-any?` _list_ _item1_ _item2_ _item3_ ... `)` : check if any of the _items_ are in the _list_ (the non-variadic form is the same as `lt:member?`). This is provided in the interests of a command that is more efficient than `lt:member?` _list_ _item1_ `or lt:member?` _list_ _item2_ `or lt:member?` _list_ _item3_ `or` ... through only searching the list once.
  + `lt:modes` _list_ : return a NetLogo list containing the items in _list_ each of which occurs (equally) the most frequently.
  + `lt:quartiles` _list_ : return the lower quartile, median, and upper quartile of the numbers in _list_ as a NetLogo list in the order given. These are calculated using the 'Tukey's handles' method. If the _list_ is empty, all elements in the result will be `Double.NaN`. If the _list_ has length 1, then the upper and lower quartiles will be `Double.NaN`. You can check of a number is `Double.NaN` with `(word` _variable_ `) = "NaN"`.
  + `lt:reduce` _reporter_ _list_ : use the _reporter_ to reduce the _list_ to a scalar, much as per NetLogo's `reduce` command.
  + `lt:sum` _list_ : return a sum of all the numbers in the _list_, or `Double.NaN` if there aren't any. You can test for this with `(word` _variable_ `) = "NaN"`.

## Conversion commands

  + `lt:as-list` _list_ : return the mutable list as an immutable NetLogo list
  + `lt:as-list-deeply` _list_ : return the mutable list as an immutable NetLogo list, recursively converting any mutable list elements of that list into immutable NetLogo lists as well
