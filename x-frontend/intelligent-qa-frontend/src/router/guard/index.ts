import useCachedGuard from './cached'
import useVisitedGuard from './visited'
import usePerimission from './perimission'

export default function useRouterGuard() {
  useVisitedGuard()
  useCachedGuard()
  usePerimission()
}
