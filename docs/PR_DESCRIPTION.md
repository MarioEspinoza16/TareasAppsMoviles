## MiBalance — Task 5

The transaction form now supports complete CRUD in one Activity. Users can search by description or ID across all months, select a transaction and edit its fields. Updates and deletions require a confirmation dialog; cancelling leaves the stored transaction unchanged. The detail screen also uses a deletion dialog.

- The Task 4 application is included as the baseline because it has not been merged into main.
- History and recent transaction rows show the full transaction ID and a Copy ID button, so users can look up a known movement. Copying does not open the detail screen.
- New mode clears the form and generates a new ID on save. Editing preserves the selected ID, including after Activity recreation.
- Existing financial validation, monthly totals, filters and Spanish/English UI resources are retained. Source code identifiers are in English.
- Transactions remain in memory for the app process lifetime. This delivery implements the CRUD, search and dialogs shown in the supplied Task 5 instructions.
- JUnit and Robolectric tests cover amounts, summaries, repository search, CRUD, confirmation cancellation and form recreation. See `docs/VALIDATION.md` for actual execution results and `docs/MANUAL_TESTS.md` for the device checklist.

Base: `main`. Branch/title: `task5-MarioEspinoza-MiBalance`. Professor reviewer: `ebarahonautn`. Merge only after the professor approves; submit the PR URL in campus.
