package com.github.blindpirate.gogradle.vcs.mercurial

import com.github.blindpirate.gogradle.GogradleGlobal
import com.github.blindpirate.gogradle.GogradleRunner
import com.github.blindpirate.gogradle.core.dependency.install.DependencyInstaller
import com.github.blindpirate.gogradle.support.WithMockInjector
import com.github.blindpirate.gogradle.vcs.Mercurial
import com.github.blindpirate.gogradle.vcs.VcsType
import com.google.inject.Key
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(GogradleRunner)
class MercurialResolvedDependencyTest {
    @Test
    @WithMockInjector
    void 'correct installer class should be returned'() {
        DependencyInstaller installer = Mockito.mock(DependencyInstaller)
        Mockito.when(GogradleGlobal.INSTANCE.getInjector().getInstance(Key.get(DependencyInstaller, Mercurial))).thenReturn(installer)
        assert new MercurialResolvedDependency(null, null, null, 0L).installer == installer
    }

    @Test
    void 'correct vcs type should be returned'() {
        assert new MercurialResolvedDependency(null, null, null, 0L).vcsType == VcsType.MERCURIAL
    }
}
