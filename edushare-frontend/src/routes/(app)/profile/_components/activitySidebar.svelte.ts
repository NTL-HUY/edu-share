export type ActivitySubTab =
  | 'knowledge'
  | 'followers'
  | 'following'

export class ActivitySidebarState {
  activeSubTab = $state<ActivitySubTab>('knowledge');

  counts = $state({
    lessons: 0,
    questions: 0,
    comments: 0,
    votes: 0,
    followers: 0,
    following: 0,
    ai_chunks: 0
  });

  setSubTab(tab: ActivitySubTab) {
    this.activeSubTab = tab;
  }
}

export const sidebarState = new ActivitySidebarState();