package com.github.blindpirate.gogradle.core.dependency

import com.github.blindpirate.gogradle.GogradleRunner
import com.github.blindpirate.gogradle.WithProject
import com.github.blindpirate.gogradle.core.VcsTempFileModule
import com.github.blindpirate.gogradle.core.dependency.parse.NotationParser
import org.gradle.api.Project
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock

import static com.github.blindpirate.gogradle.util.DependencyUtils.asGolangDependencySet
import static com.github.blindpirate.gogradle.util.DependencyUtils.mockDependency
import static org.mockito.Mockito.when

@WithProject
@RunWith(GogradleRunner)
class DefaultLockedDependencyManagerTest {

    Project project

    @Mock
    NotationParser notationParser

    @Mock
    VcsTempFileModule module1

    @Mock
    VcsTempFileModule module2

    DefaultLockedDependencyManager manager

    @Before
    void setUp() {
        manager = new DefaultLockedDependencyManager(project, notationParser)
        when(module1.getName()).thenReturn('github.com/a/b')
        when(module2.getName()).thenReturn('github.com/b/b')

        when(module1.toLockNotation()).thenReturn([name: 'github.com/a/b'])
        when(module2.toLockNotation()).thenReturn([name: 'github.com/b/c'])
    }

    @Test
    void 'getting locked dependencies should success'() {
        // given
        def notation1 = [name: 'github.com/a/b']
        def notation2 = [name: 'github.com/b/c']
        def dependency1 = mockDependency('github.com/a/b')
        def dependency2 = mockDependency('github.com/b/c')
        project.gradle.ext.lock = [notation1, notation2]
        project.gradle.ext.lock = [notation1, notation2]
        when(notationParser.produce(notation1)).thenReturn(dependency1)
        when(notationParser.produce(notation2)).thenReturn(dependency2)

        // when
        GolangDependencySet result = manager.getLockedDependencies().get();

        // then
        assert result.size() == 2
        assert result.any { it.name == 'github.com/a/b' }
        assert result.any { it.name == 'github.com/b/c' }
    }

    @Test
    void 'locked dependencies should be absent if not set'() {
        assert !manager.getLockedDependencies().isPresent()
    }

    @Test
    void 'locked dependencies should be absent if not set properly'() {
        // given
        project.gradle.ext.lock = 1

        // then
        assert !manager.getLockedDependencies().isPresent()
    }

    @Test
    void 'locked dependencies should be empty when emtpy list set'() {
        // given
        project.gradle.ext.lock = []

        // then
        assert manager.getLockedDependencies().get().isEmpty()
    }

    @Test
    void 'appending to an existing settings.gradle should success'() {
        // given
        GolangDependencySet dependencies = asGolangDependencySet(module1, module2)
        File settingsDotGradle = project.getRootDir().toPath().resolve('settings.gradle').toFile()
        settingsDotGradle.write('''\
Original
Content
''')

        // when
        manager.lock(dependencies)

        // then
        assert settingsDotGradle.getText() == '''\
Original
Content
gradle.ext.lock=// The following lines are auto-generated by gogradle, you should NEVER modify them manually.
[
['name':'github.com/a/b'],
['name':'github.com/b/c'],
]'''
    }

    @Test
    void 'replacing dependencies in settings.gradle should success'() {
        // given
        GolangDependencySet dependencies = asGolangDependencySet(module1, module2)
        def settingsDotGradle = project.getRootDir().toPath().resolve('settings.gradle').toFile()
        settingsDotGradle.write ('''\
Original
Content
gradle.ext.lock=
[
[:],
[:],
]
''')

        // when
        manager.lock(dependencies)

        // then
        assert settingsDotGradle.getText() == '''\
Original
Content
gradle.ext.lock=// The following lines are auto-generated by gogradle, you should NEVER modify them manually.
[
['name':'github.com/a/b'],
['name':'github.com/b/c'],
]'''

    }

    @Test
    void 'settings.gradle should be created if not exist'() {
        // given
        GolangDependencySet dependencies = asGolangDependencySet(module1, module2)

        // when
        manager.lock(dependencies)

        // then
        def settingsDotGradle = project.getRootDir().toPath().resolve('settings.gradle').toFile()
        assert settingsDotGradle.getText() == '''\
gradle.ext.lock=// The following lines are auto-generated by gogradle, you should NEVER modify them manually.
[
['name':'github.com/a/b'],
['name':'github.com/b/c'],
]'''
    }
}
