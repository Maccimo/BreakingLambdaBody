Lambda instantiation order vs. class redefinition
=================================================

Inspired by [https://habr.com/company/odnoklassniki/blog/429040/](https://habr.com/company/odnoklassniki/blog/429040/) (in Russian).

Changing lambda instantiation order while not prevent class redefinition via [Instrumentation API](https://docs.oracle.com/javase/10/docs/api/java/lang/instrument/package-summary.html) may lead to some tricky bugs.

Lambda's bodies live inside synthetic methods of the enclosing class and referred from lambda instance by their names in the form of `lambda$0`.
In case we change order of lambda instantiation above mentioned method names may be reassigned according to the program order.
This, in turn, may lead to wrong lambda calls.
