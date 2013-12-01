============================
 External Android libraries
============================

Example: numberpicker
=====================


Well currently we have to add it as a Android Library project using this approach:

http://developer.android.com/tools/projects/index.html#LibraryProjects

Because the library uses res files its not possible to simple build it as a jar file - for example by adding to the build.ml:

::

    <target name="jar" depends="debug">
      <jar
	  destfile="bin/numberpickerlib.jar"
	  basedir="bin/classes"
      />
    </target>

And then

::

    $ ant jar -lib ./libs 



Niece to have
=============

Well if we get a Maven build setup for the whole project it should not be necessary to include Android Library project like we currently do.
