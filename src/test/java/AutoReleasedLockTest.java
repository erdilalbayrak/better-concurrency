import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.locks.ReentrantLock;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AutoReleasedLockTest {

    private final ReentrantLock lock = new ReentrantLock();
    @Rule
    public ExpectedException exceptionAsserter = ExpectedException.none();

    @Test
    public void initialLockStateIsLocked() {
        AutoReleasedLock autoReleasedLock = new AutoReleasedLock(lock);
        assertThat(autoReleasedLock.isLocked(), is(true));
        assertThat(lock.isLocked(), is(true));
    }

    @Test
    public void lock() {
        AutoReleasedLock autoReleasedLock = new AutoReleasedLock(lock);
        autoReleasedLock.unlock();
        assertThat(autoReleasedLock.isLocked(), is(false));
        autoReleasedLock.lock();
        assertThat(autoReleasedLock.isLocked(), is(true));
        assertThat(lock.isLocked(), is(true));
    }

    @Test
    public void unlock() {
        AutoReleasedLock autoReleasedLock = new AutoReleasedLock(lock);
        autoReleasedLock.unlock();
        assertThat(autoReleasedLock.isLocked(), is(false));
        assertThat(lock.isLocked(), is(false));
    }

    @Test
    public void ifALockedLockIsLockedAgainItThrowsIllegalMonitorStateException() {
        AutoReleasedLock autoReleasedLock = new AutoReleasedLock(lock);

        exceptionAsserter.expect(IllegalMonitorStateException.class);
        exceptionAsserter.expectMessage("the lock is already locked");
        autoReleasedLock.lock();
    }

    @Test
    public void ifAnUnlockedLockIsUnlockedAgainItThrowsIllegalMonitorStateException() {
        AutoReleasedLock autoReleasedLock = new AutoReleasedLock(lock);
        autoReleasedLock.unlock();

        exceptionAsserter.expect(IllegalMonitorStateException.class);
        exceptionAsserter.expectMessage("the lock is already unlocked");
        autoReleasedLock.unlock();
    }

    @Test
    public void lockIsAutoClosable() {
        try (final AutoReleasedLock autoReleasedLock = new AutoReleasedLock(lock)) {
            assertThat(autoReleasedLock.isLocked(), is(true));
            assertThat(lock.isLocked(), is(true));
        }
        assertThat(lock.isLocked(), is(false));
    }

    @Test
    public void lockCanBeClosedManually() {
        final AutoReleasedLock autoReleasedLock = new AutoReleasedLock(lock);
        assertThat(autoReleasedLock.isLocked(), is(true));
        assertThat(lock.isLocked(), is(true));

        autoReleasedLock.close();
        assertThat(autoReleasedLock.isLocked(), is(false));
        assertThat(lock.isLocked(), is(false));

    }

    @Test
    public void evenIfLockIsReleasedManuallyTheAutoCloseIsNotAffected() {
        AutoReleasedLock autoReleasedLock = new AutoReleasedLock(lock);
        try (autoReleasedLock) {
            assertThat(autoReleasedLock.isLocked(), is(true));
            assertThat(lock.isLocked(), is(true));

            autoReleasedLock.close();
            assertThat(autoReleasedLock.isLocked(), is(false));
            assertThat(lock.isLocked(), is(false));
        }
        assertThat(autoReleasedLock.isLocked(), is(false));
        assertThat(lock.isLocked(), is(false));
    }
}
