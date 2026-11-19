## MiBalance — Task 5

Users can open Transactions from the dashboard or the editor. This screen lists all recorded movements across all dates, with each sequential numeric ID beside its name. The search field above the list matches an exact ID; Show all restores the full list. Selecting a row opens its detail, where users can edit or delete it.

- Income and expense records share IDs 1, 2, 3 and so on, assigned when saving. Updating keeps the ID; deletion does not renumber records or reuse an ID.
- Saving a new or edited transaction returns to the list. Returning to an existing Transactions screen clears its previous search so the saved movement is visible.
- The editor requires a transaction name/description. Updates and deletions use confirm/cancel dialogs; cancelling leaves stored data unchanged.
- The monthly dashboard, amount validation, vehicle categories and Spanish/English UI remain available. Source code identifiers are in English.
- The repository remains in memory: ending the app process clears records and restarts the counter. This delivery does not add Room.
- Task 4 is included as the baseline because it has not been merged into main.

See `docs/VALIDATION.md` for automated results and `docs/MANUAL_TESTS.md` for the physical device checklist. Branch/title: `task5-MarioEspinoza-MiBalance`; base: `main`. Professor reviewer: `ebarahonautn`. Merge only after professor approval; submit the PR URL in campus.
